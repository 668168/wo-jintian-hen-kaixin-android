#!/usr/bin/env python3
"""Extract readable paragraphs from happy.epub into the Android Markdown asset."""

from __future__ import annotations

import argparse
import re
import zipfile
from pathlib import Path
from xml.etree import ElementTree


BODY_FILES = [f"index_split_{number:03}.html" for number in range(2, 93)]
BLOCK_TAGS = {"p", "blockquote"}


def local_name(tag: str) -> str:
    return tag.rsplit("}", 1)[-1]


def clean_text(element: ElementTree.Element) -> str:
    text = "".join(element.itertext()).replace("\u00a0", " ")
    return re.sub(r"\s+", " ", text).strip()


def extract_blocks(element: ElementTree.Element) -> list[str]:
    paragraphs: list[str] = []

    def visit(node: ElementTree.Element) -> None:
        tag = local_name(node.tag)
        if tag == "p":
            text = clean_text(node)
            if text:
                paragraphs.append(text)
            return

        if tag == "blockquote":
            contains_blocks = any(
                local_name(child.tag) in BLOCK_TAGS for child in node.iter() if child is not node
            )
            if not contains_blocks:
                text = clean_text(node)
                if text:
                    paragraphs.append(text)
                return

        for child in node:
            visit(child)

    visit(element)
    return paragraphs


def extract_epub(epub_path: Path) -> list[str]:
    paragraphs: list[str] = []
    with zipfile.ZipFile(epub_path) as epub:
        available = set(epub.namelist())
        for name in BODY_FILES:
            if name not in available:
                continue
            root = ElementTree.fromstring(epub.read(name))
            body = next((node for node in root.iter() if local_name(node.tag) == "body"), None)
            if body is not None:
                paragraphs.extend(extract_blocks(body))
    return paragraphs


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("epub", type=Path)
    parser.add_argument("output", type=Path)
    args = parser.parse_args()

    paragraphs = extract_epub(args.epub)
    if not paragraphs:
        raise SystemExit("No paragraphs found in EPUB")

    markdown = [
        "# 《幸福的方法》段落",
        "",
        "按电子书正文顺序整理；每个引用块是一段，供 App 随机展示和前后翻阅。",
        "",
    ]
    for paragraph in paragraphs:
        markdown.extend((f"> {paragraph}", ""))

    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text("\n".join(markdown), encoding="utf-8")
    print(f"Wrote {len(paragraphs)} paragraphs to {args.output}")


if __name__ == "__main__":
    main()
