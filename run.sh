#!/bin/bash
set -e

SRC_DIR="$(pwd)"
LIB_DIR="$SRC_DIR/target/dependency"
CLASSES_DIR="$SRC_DIR/target/classes"

mkdir -p "$CLASSES_DIR"
mkdir -p "$LIB_DIR"

if [ ! -f "$LIB_DIR/mysql-connector-j-9.2.0.jar" ]; then
    echo "Downloading MySQL Connector..."
    curl -s -L -o "$LIB_DIR/mysql-connector-j-9.2.0.jar" "https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/9.2.0/mysql-connector-j-9.2.0.jar"
fi

if [ ! -f "$LIB_DIR/json-20250107.jar" ]; then
    echo "Downloading JSON library..."
    curl -s -L -o "$LIB_DIR/json-20250107.jar" "https://repo1.maven.org/maven2/org/json/json/20250107/json-20250107.jar"
fi

echo "Compiling Java files..."
find "$SRC_DIR/src/main/java" -name "*.java" -exec echo '"{}"' \; > "$SRC_DIR/sources.txt"
javac -cp "$LIB_DIR/*" -d "$CLASSES_DIR" @"$SRC_DIR/sources.txt"
rm -f "$SRC_DIR/sources.txt"

echo "Copying resources..."
if [ -d "$SRC_DIR/src/main/resources" ]; then
    cp -R "$SRC_DIR/src/main/resources/"* "$CLASSES_DIR/" 2>/dev/null || true
fi

echo "Starting Application..."
java -cp "$CLASSES_DIR:$LIB_DIR/*" org.example.hostelsystem.HostelSystemApplication
