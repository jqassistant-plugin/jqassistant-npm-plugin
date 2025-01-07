# `:NPM:Binary` Node  
  
-> represents information about executable files of the package that should be installed in PATH
  
  
## Properties  
  
| Name   | Description        |
| ------ | ------------------ |
| `name` | name of the binary |
| `path` | id of the AsyncAPI |


  
## Relations  
  
| Name                 | Target Label(s)                                      | Cardinality | Description                    |     |
| -------------------- | ---------------------------------------------------- | ----------- | ------------------------------ | --- |
| `DEFINES_INFO`       | [[Node - AsyncAPI Info\|:AsyncAPI:Info]]<br>         | 0..1        | the info of the AsyncApi       |     |
| `DEFINES_COMPONENTS` | [[Node - AsyncAPI Components\|:AsyncAPI:Components]] | 0..1        | all components of the AsyncApi |     |
| `DEFINES_CHANNEL`    | [[Node - AsyncAPI Channel\|:AsyncAPI:Channel]]       | 0..*        | a channel of the AsyncApi      |     |
| `DEFINES_OPERATION`  | [[Node - AsyncAPI Operation\|:AsyncAPI:Operation]]   | 0..*        | an operation of the AsyncApi   |     |
| `DEFINES_SERVER`     | [[Node - AsyncAPI Server\|:AsyncAPI:Server]]         | 0..*        | a server of the AsyncApi       |     |