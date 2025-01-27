# `:NPM:Package Node  
  
-> represents information about executable files of the package that should be installed in PATH
  
  
## Properties  
  
| Name          | Description                                                                                                      |
| ------------- | ---------------------------------------------------------------------------------------------------------------- |
| `fileName`    | file name, relative to the scanned directory                                                                     |
| `name`        | name of the package                                                                                              |
| `version`     | version of the package                                                                                           |
| `description` | description of the package                                                                                       |
| `keywords`    | keywords of the package                                                                                          |
| `homepage`    | homepage of the package                                                                                          |
| `license`     | license of the package                                                                                           |
| `files`       | array of file patterns that describes the entries to be included when your package is installed as a dependency  |
| `main`        | module id that is the primary entry point to the package                                                         |
| `browser`     | module id that is the entry point in case of a client-side use of the module                                     |
| `private`     | specifies if the package is private; if **true**, npm will prevent it from being published or installed publicly |


  
## Relations  
  
| Name                       | Target Label(s)                                  | Cardinality | Description                                                      |
| -------------------------- | ------------------------------------------------ | ----------- | ---------------------------------------------------------------- |
| `HAS_EXPORT`               | [[Node - NPM Export\|:NPM:Export]]<br>           | 0..*        | entry point(s) of the package                                    |
| `HAS_BINARY`               | [[Node - NPM Binary\|:NPM:Binary]]               | 0..*        | executable files of the package that should be installed in PATH |
| `HAS_MAN`                  | [[Node - NPM Man\|:NPM:Man]]                     | 0..*        | a file for the man program to find                               |
| `IN_REPOSITORY`            | [[Node - NPM Repository\|:NPM:Repository]]       | 0..1        | the repository of the package                                    |
| `HAS_BUG_TRACKER`          | [[Node - NPM BugTracker\|:NPM:BugTracker]]       | 0..1        | where to report bugs                                             |
| `HAS_AUTHOR`               | [[Node - NPM Person\|:NPM:Person]]               | 0..1        | the author of the package                                        |
| `HAS_CONTRIBUTOR`          | [[Node - NPM Person\|:NPM:Person]]               | 0..*        | contributors of the package                                      |
| `HAS_FUNDING`              | [[Node - NPM Funding\|:NPM:Funding]]             | 0..*        | funding information                                              |
| `DECLRES_SCRIPT`           | [[Node - NPM Script\|:NPM:Script]]               | 0..*        | script(s) of the package                                         |
| `DECLARES_CONFIG`          | [[Node - NPM Config\|:NPM:Config]]               | 0..*        | configuration parameters used in package scripts                 |
| `DECLARES_DEPENDENCY`      | [[Node - NPM Dependency\|:NPM:Dependency]]       | 0..*        | a dependency of the package                                      |
| `DECLARES_DEV_DEPENDENCY`  | [[Node - NPM Dependency\|:NPM:Dependency]]       | 0..*        | additional items                                                 |
| `DECLARES_PEER_DEPENDENCY` | [[Node - NPM Dependency\|:NPM:Dependency]]       | 0..*        | compatibilities of the package (plugins)                         |
| `HAS_BUNDLED_DEPENDENCY`   | [[Node - NPM Dependency\|:NPM:Dependency]]       | 0..*        | array of package names to be bundled when publishing the package |
| `HAS_OVERRIDES`            | [[Node - NPM Overrides\|:NPM:Overrides]]         | 0..*        | specific change(s) to dependencies                               |
| `DECLARES_ENGINE`          | [[Node - NPM Engine\|:NPM:Engine]]               | 0..*        | current supported versions of node or npm  of the package        |
| `DECLARES_OS`              | [[Node - NPM Os\|:NPM:Os]]                       | 0..*        | the operating systems the module will run on                     |
| `DECLARES_CPU`             | [[Node - NPM Cpu\|:NPM:Cpu]]                     | 0..*        | cpu architectures the code runs on                               |
| `DECLARES_DEV_ENGINE`      | [[Node - NPM DevEngine\|:NPM:DevEngine]]         | 0..*        | a developer engine                                               |
| `DECLARES_PUBLISH_CONFIG`  | [[Node - NPM PublishConfig\|:NPM:PublishConfig]] | 0..*        | set of config values used in publish-time                        |
| `DECLARES_WORKSPACE`       | [[Node - NPM Workspace\|:NPM:Workspace]]         | 0..*        | locations of the workspace of the package                        |
