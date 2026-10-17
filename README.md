# Threadbound

A small Java desktop app that generates randomized mazes using depth-first search and backtracking. Enter the maze dimensions, generate a maze, and explore the black walls and white paths.

## Features

- Generate a new randomized maze from a desktop interface
- Choose rectangular dimensions, such as `12x20`
- Enter one number, such as `12`, to create a square maze
- View the maze with black walls and white paths
- Entrance and exit openings are generated automatically

## Requirements

- Java 25 or later
- Maven

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar target/test-1.0-SNAPSHOT.jar
```

Enter dimensions like `12x20`, then click **Generate maze** or press Enter. Dimensions are limited to 100 × 100 cells.

## How it works

The app uses randomized depth-first search with backtracking. It explores neighboring cells, carving a passage when it reaches an unvisited cell. When it reaches a dead end, it backtracks until it can continue exploring.

## License

No license has been added yet.
