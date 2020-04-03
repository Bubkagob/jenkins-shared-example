def testPy() {
//   final pythonContent = libraryResource('com/mkobit/sharedlib/my_file.py')
//   // There are definitely better ways to do this without having to write to the consumer's workspace
//   writeFile(file: 'my_file.py', text: pythonContent)
//   sh('chmod +x my_file.py && ./my_file.py')
    sh("hostname; uname -r; ls -la")
}