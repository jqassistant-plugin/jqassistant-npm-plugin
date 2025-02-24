# `jqassistant-plugin-npm:ResolveWorkspaces` Concept

-> Directories defined as workspace must only be treated as npm workspaces if they contain a valid `package.json`. If a workspace-defined directory does not have a valid `package.json` file, this concept will delete the workspace node and deletes the belonging relation.

## Required Concepts & Used Nodes

- [[Node - NPM Package]]
- [[Node - NPM Workspace]]

## Query

```cypher
MATCH  
   (package:NPM:Package)-[rel:DECLARES_WORKSPACE]->(dir:NPM:Workspace:Directory)  
WHERE NOT EXISTS {  
MATCH  
  (dir)-[:CONTAINS]->(packageJSON:File:Json)  
WHERE  
  packageJSON.fileName ENDS WITH 'package.json'  
  }  
WITH dir, rel, package  
DETACH DELETE  dir  
DELETE rel  
WITH package  
MATCH (package)-[:DECLARES_WORKSPACE]->(workspaces:Workspace)  
RETURN workspaces AS workspaceDirectories
```
