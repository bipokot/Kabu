package io.kabu.backend.provider.group

internal fun renameClashingParametersNames(list: List<String>): List<String> {
    if (list.size < 2) return list // too small list to have name clashes

    val mutableList = list.toMutableList()

    // for each group of parameters conflicting by name -
    // change names of these parameters, avoiding possible new conflicts
    list.groupBy { it }
        .filter { it.value.size > 1 } // handle groups with conflicting names only
        .forEach { group ->
            // changing a name for each parameter of a group
            group.value.forEach { parameter ->
                var index = 1
                var newName: String

                // finding a non-conflicting name
                do {
                    newName = "${parameter}${index++}"
                    val newNameConflictsWithExistingOne = mutableList.any { it == newName }
                } while (newNameConflictsWithExistingOne)

                // setting new name to new list
                mutableList[mutableList.indexOf(parameter)] = newName
            }
        }

    return mutableList
}

