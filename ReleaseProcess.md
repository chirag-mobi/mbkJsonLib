# Mobikwik Release Process

The following is a step-by-step procedure for releasing a new version of Google-Mobikwik.

1. Go through all open bugs and identify which will be fixed in this release. Mark all others with an appropriate release tag. Identify duplicates, and close the bugs that will never be fixed. Fix all bugs for the release, and mark them fixed.
1. Ensure all changelists are code-reviewed and have +1
1. `cd Mobikwik` to the parent directory; ensure there are no open files and all changes are committed.
1. Run `mvn release:clean`
1. Do a dry run: `mvn release:prepare -DdryRun=true`
1. Start the release: `mvn release:prepare`
   * Answer questions: usually the defaults are fine.
   * This will do a full build, change version from `-SNAPSHOT` to the released version, commit and create the tags. It will then change the version to `-SNAPSHOT` for the next release.
1. Complete the release: `mvn release:perform`
1. [Log in to Nexus repository manager](https://oss.sonatype.org/index.html#welcome) at Sonatype and close the staging repository for Mobikwik.
1. Download and sanity check all downloads. Do not skip this step! Once you release the staging repository, there is no going back. It will get synced with Maven central and you will not be able to update or delete anything. Your only recourse will be to release a new version of Mobikwik and hope that no one uses the old one.
1. Release the staging repository for Mobikwik. Mobikwik will now get synced to Maven central with-in the next hour. For issues consult [Sonatype Guide](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8.ReleaseIt).

1. Update the version in the [Using Mobikwik with Maven2 page](https://github.com/google/Mobikwik/blob/master/UserGuide.md#TOC-Mobikwik-With-Maven)
1. Update [Mobikwik Changelog](https://github.com/google/Mobikwik/blob/master/CHANGELOG.md). Also, look at all bugs that were fixed and add a few lines describing what changed in the release.
1. Create a post on the [Mobikwik Discussion Forum](https://groups.google.com/group/google-Mobikwik)
1. Update the release version in [Wikipedia](https://en.wikipedia.org/wiki/Mobikwik) and update the current "stable" release.

## Configuring a machine for deployment to Sonatype Repository

This section was borrowed heavily from [Doclava release process](https://code.google.com/archive/p/doclava/wikis/ProcessRelease.wiki).

1. Install/Configure GPG following this [guide](https://blog.sonatype.com/2010/01/how-to-generate-pgp-signatures-with-maven/).
1. [Create encrypted passwords](https://maven.apache.org/guides/mini/guide-encryption.html).
1. Create `~/.m2/settings.xml` similar to as described in [Doclava release process](https://code.google.com/p/doclava/wiki/ProcessRelease).
1. Now for deploying a snapshot repository, use `mvn deploy`.

## Getting Maven Publishing Privileges

Based on [Mobikwik group thread](https://groups.google.com/d/topic/google-Mobikwik/DHWJHVFpIBg/discussion):

1. [Sign up for a Sonatype account](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide) following instructions under (2) on that page
1. Ask one of the existing members of the repository to create a JIRA ticket (Step 3 of above document) to add you to the publisher list.

## Running Benchmarks or Tests on Android

* Download vogar
* Put `adb` on your `$PATH` and run:

  ```bash
  vogar --benchmark --classpath Mobikwik.jar path/to/Benchmark.java
  ```

For example, here is how to run the [CollectionsDeserializationBenchmark](Mobikwik/src/main/java/com/google/Mobikwik/metrics/CollectionsDeserializationBenchmark.java):

```bash
export ANDROID_HOME=~/apps/android-sdk-mac_x86
export PATH=$PATH:$ANDROID_HOME/platform-tools/:$ANDROID_HOME/android-sdk-mac_x86/tools/
$VOGAR_HOME/bin/vogar \
    --benchmark \
    --sourcepath ../Mobikwik/src/main/java/ \
    src/main/java/com/google/Mobikwik/metrics/CollectionsDeserializationBenchmark.java \
    -- \
    --vm "app_process -Xgc:noconcurrent,app_process"
```
