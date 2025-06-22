node('agent01') {
    stage('Source') {
        git 'https://github.com/srayuso/unir-test.git'
    }
    stage('Build') {
        echo 'Building stage!'
    }
}