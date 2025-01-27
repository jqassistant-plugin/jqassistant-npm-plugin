# `:NPM:DevEngine` Node  
  
-> represents a developer engine
  
## Properties  
  
| Name      | Description                                                                                |
| --------- | ------------------------------------------------------------------------------------------ |
| `name`    | name of the devEngine object                                                               |
| `type`    | key of the devEngine object - either *cpu*, *os*, *libc*, *runtime*, or *packageManager*   |
| `version` | version of the devEngine object                                                            |
| `onFail`  | action to take when a task fails - either *warn*, *error* or *ignore*; defaults to *error* |
