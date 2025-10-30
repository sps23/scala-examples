# GitHub Actions for Documentation Publishing

This repository includes automated workflows to publish documentation to GitHub Pages.

## Available Workflows

### 1. `publish-docs-simple.yml` (Recommended)
**Simpler setup** - Uses the GitHub token automatically, no SSH keys needed.

**Features:**
- ✅ Runs on demand from GitHub Actions tab
- ✅ Compiles the entire project
- ✅ Generates unified Scaladoc from all submodules
- ✅ Creates the complete site with custom landing page
- ✅ Publishes to `gh-pages` branch
- ✅ No additional setup required

**How to use:**
1. Go to your repository on GitHub
2. Click **Actions** tab
3. Select **"Publish Documentation (Simple)"**
4. Click **"Run workflow"** button
5. Wait for completion (usually 2-5 minutes)
6. Visit https://sylwesterstocki.github.io/scala-examples/

### 2. `publish-docs.yml` (Advanced)
Uses sbt's built-in `ghpagesPushSite` task. Requires SSH key setup.

**Additional Setup Required:**
1. Generate an SSH key pair:
   ```bash
   ssh-keygen -t ed25519 -C "github-actions" -f gh-pages-deploy-key
   ```

2. Add the **public key** (`gh-pages-deploy-key.pub`) as a Deploy Key:
   - Go to repository **Settings** → **Deploy keys**
   - Click **Add deploy key**
   - Title: "GitHub Actions Deploy Key"
   - Paste the public key content
   - ✅ Check "Allow write access"
   - Click **Add key**

3. Add the **private key** as a Secret:
   - Go to repository **Settings** → **Secrets and variables** → **Actions**
   - Click **New repository secret**
   - Name: `GH_PAGES_DEPLOY_KEY`
   - Value: Paste the entire private key content
   - Click **Add secret**

## Running the Workflows

### Manually (On Demand)
1. Navigate to **Actions** tab in GitHub
2. Select the workflow you want to run
3. Click **"Run workflow"** dropdown
4. Select branch (usually `main`)
5. Click **"Run workflow"** button

### Automatically on Push (Optional)
To enable automatic runs on every push to main branch, uncomment these lines in the workflow file:

```yaml
on:
  workflow_dispatch:
  push:              # Uncomment this
    branches:        # Uncomment this
      - main         # Uncomment this
```

## What the Workflow Does

1. **Checkout** - Clones your repository
2. **Setup Java & sbt** - Installs JDK 21 and sbt build tool
3. **Compile** - Runs `sbt compile` to ensure code compiles
4. **Generate Docs** - Runs `sbt unidoc` to create unified API documentation
5. **Build Site** - Runs `sbt makeSite` to generate the complete website
6. **Publish** - Pushes to `gh-pages` branch
7. **Summary** - Displays success message with link

## Site Structure After Publishing

```
gh-pages branch:
├── index.html              # Custom landing page
├── api/                    # Unified Scaladoc
│   └── io/github/sps23/
│       └── zstream/
│           ├── HttpServer.html
│           ├── HealthCheckRoute.html
│           ├── SwaggerRoute.html
│           └── NumberStream.html
└── [other static files]
```

## Viewing the Published Site

After the workflow completes successfully:
- **URL**: https://sylwesterstocki.github.io/scala-examples/
- **API Docs**: https://sylwesterstocki.github.io/scala-examples/api/
- **ZIO Examples**: https://sylwesterstocki.github.io/scala-examples/api/io/github/sps23/zstream/

## GitHub Pages Configuration

Ensure GitHub Pages is enabled:
1. Go to repository **Settings** → **Pages**
2. Under "Source":
   - Branch: `gh-pages`
   - Folder: `/ (root)`
3. Click **Save**

The `gh-pages` branch will be created automatically by the workflow on first run.

## Troubleshooting

### Workflow Fails at "Setup sbt"
- This is usually temporary. Try re-running the workflow.
- The apt repository might be temporarily unavailable.

### "Permission denied" Error
- For **simple workflow**: Check that Actions have write permissions
  - Settings → Actions → General → Workflow permissions
  - Select "Read and write permissions"
- For **advanced workflow**: Verify SSH key is properly configured

### Site Not Updating
- Wait 2-3 minutes after workflow completes
- Check Actions tab for any errors
- Clear browser cache

### Documentation Not Generated
- Check that all `.scala` files have proper syntax
- Look at the workflow logs for compilation errors
- Ensure `build.sbt` has no errors

## Monitoring Workflow Runs

1. Go to **Actions** tab
2. Click on a workflow run to see details
3. Click on the job name to see logs
4. Each step shows detailed output

## Local Testing

Before pushing to GitHub, test locally:

```bash
# Generate documentation
sbt makeSite

# Preview the site
open target/site/index.html

# If everything looks good, commit and push
git add .
git commit -m "Update documentation"
git push

# Then run the GitHub Action
```

## Recommended Workflow

1. **Develop locally** - Write code and add Scaladoc comments
2. **Test locally** - Run `sbt makeSite` to preview
3. **Commit & push** - Push changes to GitHub
4. **Run workflow** - Manually trigger the workflow from Actions tab
5. **Verify** - Check the published site

## CI/CD Integration

To make documentation updates part of your CI/CD pipeline:
- Uncomment the `push` trigger in the workflow
- Documentation will be automatically updated on every push to main
- Useful for keeping docs always in sync with code

## Benefits

✅ **Always up-to-date** - Documentation reflects latest code  
✅ **No manual steps** - One-click publishing  
✅ **Version controlled** - All docs in git history  
✅ **Professional** - Polished landing page + API docs  
✅ **Scalable** - Easily add more modules
