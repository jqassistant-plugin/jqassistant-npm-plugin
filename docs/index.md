---
title: jQAssistant Example Plugin
---
# jQAssistant npm Plugin

This is the npm Plugin for [jQAssistant](https://jqassistant.org).

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

**Package Node:** [[Node - NPM Package|Package]]
**Field Nodes:**
- [[Node - NPM Binary|Binary]]
- [[Node - NPM BugTracker|BugTracker]]
- [[Node - NPM Config|Config]]
- [[Node - NPM Cpu|Cpu]]
- [[Node - NPM Dependency|Dependency]]
- [[Node - NPM DevEngine|DevEngine]]
- [[Node - NPM Engine|Engine]]
- [[Node - NPM Export|Export]]
- [[Node - NPM Funding|Funding]]
- [[Node - NPM Man|Man]]
- [[Node - NPM Os|Os]]
- [[Node - NPM Overrides|Overrides]]
- [[Node - NPM Person|Person]]
- [[Node - NPM PublishConfig|PublishConfig]]
- [[Node - NPM Repository|Repository]]
- [[Node - NPM Script|Script]]
- [[Node - NPM Workspace|Workspace]]
**Concepts:**
- [[Concept - NPM ResolveWorkspaces |ResolveWorkspaces]]
