def call(project_name) {
    def config = readJSON(text: libraryResource(resource:'config.json', encoding:'utf-8'), returnPojo: true)
    return config[project_name]
}
