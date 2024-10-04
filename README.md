# cli_validator


## Validation
```.bash
./gradlew build

# Check Properties file
java -cp build/libs/*.jar com.github.horitaku1124.cli_validator.PropertyValidate2Kt src/main/resource/test2.properties

# Check HTML file
java -cp build/libs/*.jar com.github.horitaku1124.cli_validator.HtmlValidateKt src/main/resource/test1.html 

```