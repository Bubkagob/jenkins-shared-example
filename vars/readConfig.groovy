def call(file_name, project_name) {
    def config = readJSON(text: libraryResource(resource: file_name, encoding:'utf-8'), returnPojo: true)
    return config[project_name]
}
