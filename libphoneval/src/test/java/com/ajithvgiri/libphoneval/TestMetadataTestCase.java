/*
 * Copyright (C) 2012 The Libphonenumber Authors
 * Copyright (C) 2017 Michael Rozumyanskiy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ajithvgiri.libphoneval;

import junit.framework.TestCase;

/**
 * Root class for PhoneNumberUtil tests that depend on the test metadata file.
 * <p>
 * Note since tests that extend this class do not use the normal metadata file, they should not be
 * used for regression test purposes.
 * <p>
 * Ideally the {@code phoneUtil} field (which uses test metadata) would be the only way that tests
 * need to interact with a PhoneNumberUtil instance. However as some static methods in the library
 * invoke "getInstance()" internally, we must also inject the test instance as the PhoneNumberUtil
 * singleton. This means it is unsafe to run tests derived from this class in parallel with each
 * other or at the same time as other tests which might require the singleton instance.
 *
 * @author Shaopeng Jia
 */
public abstract class TestMetadataTestCase extends TestCase {
  private static final String TEST_METADATA_FILE_PREFIX =
      "/com/ajithvgiri/libphoneval/data/PhoneNumberMetadataProtoForTesting";
  private static final String TEST_ALTERNATE_FORMATS_FILE_PREFIX =
      "/com/ajithvgiri/libphoneval/data/PhoneNumberAlternateFormatsProto";
  private static final String TEST_SHORT_NUMBER_METADATA_FILE_PREFIX =
      "/com/ajithvgiri/libphoneval/data/ShortNumberMetadataProto";

  /** An instance of PhoneNumberUtil that uses test metadata. */
  protected final PhoneNumberUtil phoneUtil;

  public TestMetadataTestCase() {
    phoneUtil = new PhoneNumberUtil(new MultiFileMetadataSourceImpl(
        TEST_METADATA_FILE_PREFIX, TEST_ALTERNATE_FORMATS_FILE_PREFIX, TEST_SHORT_NUMBER_METADATA_FILE_PREFIX,
        new ResourceMetadataLoader(TestMetadataTestCase.class)),
        CountryCodeToRegionCodeMapForTesting.getCountryCodeToRegionCodeMap());
  }
}
