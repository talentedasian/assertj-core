/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2021 the original author or authors.
 */
package org.assertj.core.internal.paths;

import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.createSymbolicLink;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.util.FailureMessages.actualIsNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.assertj.core.internal.PathsBaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class Paths_assertHasFileName_Test extends PathsBaseTest {

  public static FileSystemResource resource;

  private static Path existingFile;
  private static Path symlinkToExistingFile;
  private static Path nonExistingPath;
  private static Path symlinkToNonExistingPath;
  private static Path existingDirectory;
  private static Path symlinkToExistingDirectory;

  @BeforeAll
  static void initPaths() throws IOException {
    resource = new FileSystemResource();
    final FileSystem fs = resource.getFileSystem();

    existingDirectory = fs.getPath("/dir1/dir2");
    symlinkToExistingDirectory = fs.getPath("/symlinkToExistingDirectory");
    createDirectory(fs.getPath("/dir1"));
    createDirectory(existingDirectory);
    createSymbolicLink(symlinkToExistingDirectory, existingDirectory);

    existingFile = fs.getPath("/dir1/dir2/gc.log");
    symlinkToExistingFile = fs.getPath("/dir1/good-symlink");
    createFile(existingFile);
    createSymbolicLink(symlinkToExistingFile, existingFile);

    nonExistingPath = fs.getPath("/dir1/fake.log");
    symlinkToNonExistingPath = fs.getPath("/dir1/bad-symlink");
    createSymbolicLink(symlinkToNonExistingPath, nonExistingPath);
  }

  @AfterAll
  static void tearDown() {
    resource.close();
  }

  @Test
  void should_fail_if_actual_is_null() {
    assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> paths.assertHasFileName(info, null, "file.txt"))
                                                   .withMessage(actualIsNull());
  }

  @Test
  void should_fail_if_given_file_name_is_null() {
    assertThatNullPointerException().isThrownBy(() -> paths.assertHasFileName(info, existingFile, null))
                                    .withMessage("expected fileName should not be null");
  }

  @Test
  void should_pass_if_actual_file_has_the_given_file_name() {
    paths.assertHasFileName(info, existingFile, "gc.log");
  }

  @Test
  void should_pass_if_actual_non_existent_path_has_the_given_file_name() {
    paths.assertHasFileName(info, nonExistingPath, "fake.log");
  }

  @Test
  void should_pass_if_actual_symbolic_link_has_the_given_file_name() {
    paths.assertHasFileName(info, symlinkToNonExistingPath, "bad-symlink");
    paths.assertHasFileName(info, symlinkToExistingFile, "good-symlink");
  }

  @Test
  void should_pass_if_actual_directory_has_the_given_file_name() {
    paths.assertHasFileName(info, existingDirectory, "dir2");
  }
}
