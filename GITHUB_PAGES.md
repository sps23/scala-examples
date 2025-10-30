# GitHub Pages Documentation Setup

This guide explains how to generate and publish documentation to GitHub Pages for this repository.

## Overview

The project uses:
- **sbt-unidoc**: Aggregates Scaladoc from all submodules into a unified documentation
- **sbt-site**: Creates a website structure with custom pages
- **sbt-ghpages**: Publishes the site to GitHub Pages

## Prerequisites

1. You need to have SSH access to your GitHub repository configured
2. Make sure you're on the `main` branch (or your default branch)

## Generate Documentation Locally

To preview the documentation locally before publishing:

```bash
# Generate the unified documentation
sbt unidoc

# Generate the complete site (including custom index page)
sbt makeSite

# Preview the generated site
# The site will be in target/site/
open target/site/index.html
```

## Publishing to GitHub Pages

To publish the documentation to GitHub Pages:

```bash
# Generate and publish in one command
sbt ghpagesPushSite
```

This command will:
1. Generate unified Scaladoc from all submodules
2. Copy the custom index.html and other site resources
3. Commit everything to the `gh-pages` branch
4. Push to GitHub

## GitHub Repository Settings

After the first publish, you need to configure GitHub Pages:

1. Go to your repository on GitHub
2. Navigate to **Settings** → **Pages**
3. Under "Source", select:
   - Branch: `gh-pages`
   - Folder: `/ (root)`
4. Click **Save**

Your documentation will be available at:
```
https://sylwesterstocki.github.io/scala-examples/
```

## Project Structure

```
scala-examples/
├── src/site/                    # Custom website content
│   └── index.html              # Landing page
├── zio-examples/               # Submodule with ZIO examples
│   └── src/main/scala/         # Source code (documented)
└── target/site/                # Generated site (local preview)
    ├── index.html              # Landing page
    └── api/                    # Unified Scaladoc
```

## Workflow

1. Write code with Scaladoc comments
2. Run `sbt makeSite` to preview locally
3. Commit and push your changes to main branch
4. Run `sbt ghpagesPushSite` to publish documentation
5. Documentation is live at your GitHub Pages URL

## Updating Documentation

Every time you make changes to the code or documentation:

```bash
# Make your changes
git add .
git commit -m "Update documentation"
git push origin main

# Publish updated docs
sbt ghpagesPushSite
```

## Customizing the Landing Page

Edit `src/site/index.html` to customize the landing page. The file uses:
- Pure HTML/CSS (no build step required)
- Responsive design
- Links to the unified API documentation

## Troubleshooting

### Permission Denied
If you get a permission error, make sure your SSH keys are configured:
```bash
ssh -T git@github.com
```

### Site Not Updating
- Wait a few minutes for GitHub Pages to rebuild
- Check the Actions tab in GitHub for build status
- Clear your browser cache

### Documentation Not Found
Make sure the `gh-pages` branch exists and is properly configured in GitHub Settings.

