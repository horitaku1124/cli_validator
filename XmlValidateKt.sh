#!/bin/bash -e

path=$1
file=$0
dir=$(dirname $0)
java -cp ${dir}/cli_validator-1.0-SNAPSHOT.jar:. com.github.horitaku1124.cli_validator.XmlValidateKt $path
