# sas-project
sas project unito


to compile:
    - export PATH_TO_FX=/etc/javafx-sdk-22.0.2/lib/
    - javac --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml $(find main -name "*.java") -d out
    - cp -r main/ui/*.fxml out/main/ui/
    - cp -r main/ui/general/*.fxml out/main/ui/general/
    - cp -r main/ui/menu/*.fxml out/main/ui/menu/

to run:
    - java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.fxml -cp out main.CatERingApp