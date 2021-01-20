workspace(name = "grpc_example")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

#        http_archive(
#            name = "io_opencensus_cpp",
#            sha256 = "90d6fafa8b1a2ea613bf662731d3086e1c2ed286f458a95c81744df2dbae41b1",
#            strip_prefix = "opencensus-cpp-c9a4da319bc669a772928ffc55af4a61be1a1176",
#            urls = [
#                "https://storage.googleapis.com/grpc-bazel-mirror/github.com/census-instrumentation/opencensus-cpp/archive/c9a4da319bc669a772928ffc55af4a61be1a1176.tar.gz",
#                "https://github.com/census-instrumentation/opencensus-cpp/archive/c9a4da319bc669a772928ffc55af4a61be1a1176.tar.gz",
#            ],
#        )

http_archive(
		    name = "com_google_googleapis",
    sha256 = "150be57ff83646e5652e03683c949f0830d9a0e73ef787786864210e45537fe0",
    strip_prefix = "googleapis-6e3b55e26bf5a9f7874b6ba1411a0cc50cb87a48",
    urls = ["https://github.com/googleapis/googleapis/archive/6e3b55e26bf5a9f7874b6ba1411a0cc50cb87a48.zip"],
#		    strip_prefix = "googleapis-master",
#		            urls = ["https://github.com/googleapis/googleapis/archive/master.zip"],
#		        sha256 = "0744d1a1834ab350126b12ebe2b4bb1c8feb5883bd1ba0a6e876cb741d569994",
#			    strip_prefix = "googleapis-bcc476396e799806d3355e87246c6becf6250a70",
#			        urls = ["https://github.com/googleapis/googleapis/archive/bcc476396e799806d3355e87246c6becf6250a70.tar.gz"],
				)


# Google APIs - used by Stackdriver exporter.
load("@com_google_googleapis//:repository_rules.bzl", "switched_rules_by_language")

switched_rules_by_language(
		    name = "com_google_googleapis_imports",
		        cc = True,
			    grpc = True,
			    )
http_archive(
    name = "com_github_grpc_grpc",
    urls = [
        "https://github.com/grpc/grpc/archive/v1.31.0.tar.gz",
    ],
    strip_prefix = "grpc-1.31.0",
)

load("@com_github_grpc_grpc//bazel:grpc_deps.bzl", "grpc_deps")

grpc_deps()

load(
    "@build_bazel_rules_apple//apple:repositories.bzl",
    "apple_rules_dependencies",
)

apple_rules_dependencies()

