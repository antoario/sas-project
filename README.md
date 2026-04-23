# CatERing

## Requirements

- Java JDK
- JavaFX SDK 22.0.2 (installed at `/etc/javafx-sdk-22.0.2/`)

## Build & Run

### 1. Set the JavaFX path

```bash
export PATH_TO_FX=/etc/javafx-sdk-22.0.2/lib/
```

### 2. Compile

```bash
javac --module-path $PATH_TO_FX \
      --add-modules javafx.controls,javafx.fxml \
      $(find main -name "*.java") \
      -d out
```

### 3. Copy FXML resources

```bash
cp -r main/ui/*.fxml          out/main/ui/
cp -r main/ui/general/*.fxml  out/main/ui/general/
cp -r main/ui/menu/*.fxml     out/main/ui/menu/
```

### 4. Run

```bash
java --module-path $PATH_TO_FX \
     --add-modules javafx.controls,javafx.fxml \
     -cp out main.CatERingApp
```
