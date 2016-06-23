node() {
    git poll: false, changelog: false, url: repositoryUrl, credentialsId: "git-credentials", branch: branch
    shell = load "lib/shell.groovy"
    git = load "lib/git.groovy"
}

stage 'build development'
node() {
    git url: repositoryUrl, credentialsId: "git-credentials", branch: source
    commitId = shell.pipe("git rev-parse HEAD")
    sh("mvn clean verify")
}

stage 'promote to stable'
node() {
    git.mergeBranch(commitId, target, repositoryUrl, "git-credentials")
}
