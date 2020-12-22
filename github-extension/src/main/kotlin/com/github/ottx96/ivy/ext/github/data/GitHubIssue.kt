package com.github.ottx96.ivy.ext.github.data

import org.kohsuke.github.GHIssue

data class GitHubIssue(val data: GHIssue?,
                       val title: String = data?.title?:"",
                       val state: String = data?.state?.name?:"",
                       val repo:  String = "",
                       val author: String = data?.user?.login?:"",
                       val labels: String = data?.labels?.joinToString { "[${it.name}] " }?:"")