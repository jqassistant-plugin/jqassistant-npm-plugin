# `:NPM:Repository` Node  
  
-> represents the repository of the package
  
  
## Properties  
  
| Name   | Description      |
|--------|------------------|
| `type` | type of the repo |
| `url`  | url of the repo  |


  
## Relations  
  
| Name           | Target Label(s)  | Cardinality | Description                                                                   |
|----------------|------------------| ----------- |-------------------------------------------------------------------------------|
| `IN_DIRECTORY` | :File: Directory | 0..1        | directory of the projects package.json in case it's not in the root directory |
