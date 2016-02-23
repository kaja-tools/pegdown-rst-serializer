# Introduction

Project **pegdown-rst-serializer** enables you to convert Markdown to reStructuredText using Java.
It is built on top of (pegdown)[https://github.com/sirthias/pegdown] Java Markdown parser.

# Usage

```java
char[] md = FileUtils.readFileToString( "README.md" ).toCharArray();

PegDownProcessor pegDownProcessor = new PegDownProcessor();
RootNode astRoot = pegDownProcessor.parseMarkdown( md );
String rst = new ToRstSerializer().toRst( astRoot, md );
```