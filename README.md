# Harbour Franca

Harbour Franca is a game where you play as a travelling merchant, trading with other merchants in other ports speaking various languages. Can you understand their demands and become the richest merchant in the world?

## Play the game

Download the latest version from the [releases page](https://github.com/TemariVirus/harbour-franca/releases/latest).

Ensure you have java installed and run the following command to start the game:

```
java -jar harbour-franca.jar
```

## Background

Harbour Franca is built with libGDX and OOP principles, as part of our Object-Oriented Programming university module.

The app is split into two layers: a reusable abstract engine layer, and a game-specific layer. Engine code is in [`core/src/main/java/com/simpulator/engine/`](core/src/main/java/com/simpulator/engine/) and contains logic that is reusable for any game or simulations. Game code is in [`core/src/main/java/com/simpulator/game/`](core/src/main/java/com/simpulator/game/) and contains logic specific to Harbour Franca.

Class diagrams for all packages are available in [`class-diagram/`](class-diagram/).
