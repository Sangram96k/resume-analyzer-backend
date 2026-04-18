# GitHub Upload Instructions

## ✅ Pre-Upload Checklist

### 1. Verify .gitignore is properly configured
- ✓ `application.properties` is excluded
- ✓ IDE configuration files are excluded
- ✓ Build artifacts (`target/`, `.mvn/`) are excluded
- ✓ Environment files (`.env`, `.env.local`) are excluded

### 2. Create application.properties from template
```bash
# Copy the example file for reference (not uploaded)
cp src/main/resources/application.properties.example src/main/resources/application.properties
# Add your actual values to: application.properties
# This file will NOT be committed (covered by .gitignore)
```

### 3. Important Files to NOT Commit
- `src/main/resources/application.properties` - Contains API keys & DB passwords
- `.env` files - Any environment variable files
- `target/` - Build artifacts
- `.mvn/wrapper/maven-wrapper.jar` - Already ignored

---

## 🚀 Steps to Upload to GitHub

### Step 1: Initialize Git Repository
```bash
cd path/to/ai-agent
git init
git config user.name "Your Name"
git config user.email "your.email@github.com"
```

### Step 2: Verify Files to be Committed
```bash
git add .
git status  # Review the files - application.properties should NOT appear
git diff --cached | grep -i "password\|key\|secret"  # Safety check
```

### Step 3: Create Initial Commit
```bash
git add .
git commit -m "Initial commit: AI-powered resume analyzer agent"
```

### Step 4: Create GitHub Repository
1. Go to https://github.com/new
2. Create a new repository (e.g., `resumeanalyzer-agent`)
3. Do NOT initialize with README (you'll push existing repo)
4. Do NOT add .gitignore (we have a custom one)

### Step 5: Add Remote & Push
```bash
git remote add origin https://github.com/YOUR_USERNAME/resumeanalyzer-agent.git
git branch -M main
git push -u origin main
```

---

## 🛡️ Security Best Practices

### Local Setup for Developers
Each developer should:
1. Copy the example file: `cp application.properties.example application.properties`
2. Add their own credentials to the local `application.properties`
3. Verify `application.properties` appears in `.gitignore`

### Managing Secrets in Production
For production:
- Use GitHub Secrets for API keys
- Use CI/CD environment variables
- Never commit credentials
- Rotate keys periodically

---

## 📝 What's Included in Upload

### Committed Files:
✓ Source code (Java files)
✓ `pom.xml` (dependencies)
✓ `application.properties.example` (template only)
✓ README, HELP.md
✓ Frontend files (`static/`, `templates/`)
✓ Maven wrapper scripts (`mvnw`, `mvnw.cmd`)

### NOT Committed (Protected by .gitignore):
✗ `application.properties` (with real credentials)
✗ `target/` (build output)
✗ IDE configuration (`.idea/`, `.vscode/`)
✗ `.env` files
✗ Maven build cache (`.mvn/wrapper/maven-wrapper.jar`)

---

## ⚠️ If You Accidentally Committed Credentials

If you pushed credentials before setting up .gitignore:
```bash
# Remove file from history
git filter-branch --tree-filter 'rm -f src/main/resources/application.properties' HEAD

# Force push (be careful!)
git push origin main --force

# Rotate all exposed credentials immediately!
```

---

## ✨ Additional Recommendations

1. **Add a README.md** with:
   - Project description
   - Setup instructions
   - How to configure the application
   - Dependencies needed

2. **Add LICENSE file** (e.g., MIT, Apache 2.0)

3. **Create CONTRIBUTING.md** for collaboration guidelines

4. **Consider adding:**
   - Docker configuration
   - CI/CD workflow (.github/workflows/)
   - Documentation for API endpoints

