#!/bin/bash -e

path=$1
echo $0
dir=$(dirname /Users/tak/cli_validator/XmlValidateKt.sh)
java -cp ${dir}/cli_validator-1.0-SNAPSHOT.jar:. com.github.horitaku1124.cli_validator.XmlValidateKt $path
