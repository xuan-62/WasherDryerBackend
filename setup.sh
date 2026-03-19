#!/usr/bin/env bash
set -e

MAVEN_VERSION="3.9.9"
TOMCAT_VERSION="10.1.52"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ ! -d "$SCRIPT_DIR/maven" ]; then
    echo "Downloading Maven $MAVEN_VERSION..."
    curl -fsSL "https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" \
        | tar -xz -C "$SCRIPT_DIR"
    mv "$SCRIPT_DIR/apache-maven-${MAVEN_VERSION}" "$SCRIPT_DIR/maven"
    echo "Maven installed."
else
    echo "Maven already present, skipping."
fi

if [ ! -d "$SCRIPT_DIR/tomcat" ]; then
    echo "Downloading Tomcat $TOMCAT_VERSION..."
    curl -fsSL "https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz" \
        | tar -xz -C "$SCRIPT_DIR"
    mv "$SCRIPT_DIR/apache-tomcat-${TOMCAT_VERSION}" "$SCRIPT_DIR/tomcat"
    chmod +x "$SCRIPT_DIR/tomcat/bin/"*.sh
    echo "Tomcat installed."
else
    echo "Tomcat already present, skipping."
fi

if [ ! -f "$SCRIPT_DIR/.env" ]; then
    cp "$SCRIPT_DIR/.env.example" "$SCRIPT_DIR/.env"
    echo ".env created — fill in your credentials before starting."
fi

echo "Setup complete. Run './maven/bin/mvn clean package -DskipTests' to build."
