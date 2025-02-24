---
title: jQAssistant Example Plugin
---
# jQAssistant npm Plugin

This is the npm Plugin for [jQAssistant](https://jqassistant.org).
It provides a scanner for`package.json` files of npm projects.
## Installation

Add the plugin to the `plugins` section of the `jqassistant.yml` configuration file:

```yaml
jqassistant:
  plugins:
    # Includes the jQAssistant NPM plugin
    - group-id: org.jqassistant.plugin
      artifact-id: jqassistant-npm-plugin
      version: 2.0.0-SNAPSHOT
  scan:
    include:
      files:
        - ${project.basedir}/package.json
```

## Reference Documentation

The plugin provides a scanner which accepts files with the name `package.json` and creates the structure as described in this section, using the items defined in the [npm Docs](https://docs.npmjs.com/cli/v10/configuring-npm/package-json) and having nodes labeled with [[Node - NPM Package|:NPM:Package:Json:File]] as entry point.

**Node-Types:**

- [[Node - NPM Package|:NPM:Package:Json:File]]
	- [[Node - NPM Binary|:NPM:Binary]]
	- [[Node - NPM BugTracker|:NPM:BugTracker]]
	- [[Node - NPM Config|:NPM:Config]]
	- [[Node - NPM Cpu|:NPM:Cpu]]
	- [[Node - NPM Dependency|:NPM:Dependency]]
	- [[Node - NPM DevEngine|:NPM:DevEngine]]
	- [[Node - NPM Engine|:NPM:Engine]]
	- [[Node - NPM Export|:NPM:Export:File]]
	- [[Node - NPM Funding|:NPM:Funding]]
	- [[Node - NPM Man|:NPM:Man]]
	- [[Node - NPM Os|:NPM:Os]]
	- [[Node - NPM Overrides|:NPM:Overrides]]
	- [[Node - NPM Person|:NPM:Person]]
	- [[Node - NPM PublishConfig|:NPM:PublishConfig]]
	- [[Node - NPM Repository|:NPM:Repository]]
	- [[Node - NPM Script|:NPM:Script]]
	- [[Node - NPM Workspace|:NPM:Workspace:File:Directory]]
