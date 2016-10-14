/**
 * @license
 * Copyright 2016 Google Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// TODO(bleichen):
//   - So far only 16 byte tags are tested.
// Tested providers:
//   BC : ok
//        BC uses a 64-bit default for tags. This is not such a big
//        problem as with AES-GCM, since the tag gives 64 bit strength.

package com.google.security.wycheproof;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import junit.framework.TestCase;

/** AES-EAX tests */
public class AesEaxTest extends TestCase {

  /** Test vectors */
  public static class EaxTestVector {
    final byte[] pt;
    final byte[] aad;
    final byte[] ct;
    final String ptHex;
    final String ctHex;
    final GCMParameterSpec parameters;
    final SecretKeySpec key;

    public EaxTestVector(
        String message, String keyMaterial, String nonce, String aad, String ciphertext) {
      this.ptHex = message;
      this.pt = TestUtil.hexToBytes(message);
      this.aad = TestUtil.hexToBytes(aad);
      this.ct = TestUtil.hexToBytes(ciphertext);
      this.ctHex = ciphertext;
      // Ugly hack from
      // https://github.com/bcgit/bc-java/blob/master/prov/src/test/java/org/bouncycastle/jce/provider/test/AEADTest.java
      // Apparently, one way to specify the tag length is to use GCMParameterSpec.
      // BouncyCastle is using a 64-bit tag by default.
      // So far all test vectors use a 128 bit tag.
      this.parameters = new GCMParameterSpec(128, TestUtil.hexToBytes(nonce));
      this.key = new SecretKeySpec(TestUtil.hexToBytes(keyMaterial), "AES");
    }
  };

  private static final EaxTestVector[] EAX_TEST_VECTOR = {
    // Test vectors from
    // http://csrc.nist.gov/groups/ST/toolkit/BCM/modes_development.html
    // TODO(bleichen): Check if we can include test cases from NIST and
    //   republish.
    new EaxTestVector(
        "",
        "233952dee4d5ed5f9b9c6d6ff80ff478",
        "62ec67f9c3a4a407fcb2a8c49031a8b3",
        "6bfb914fd07eae6b",
        "e037830e8389f27b025a2d6527e79d01"),
    new EaxTestVector(
        "f7fb",
        "91945d3f4dcbee0bf45ef52255f095a4",
        "becaf043b0a23d843194ba972c66debd",
        "fa3bfd4806eb53fa",
        "19dd5c4c9331049d0bdab0277408f67967e5"),
    new EaxTestVector(
        "1a47cb4933",
        "01f74ad64077f2e704c0f60ada3dd523",
        "70c3db4f0d26368400a10ed05d2bff5e",
        "234a3463c1264ac6",
        "d851d5bae03a59f238a23e39199dc9266626c40f80"),
    new EaxTestVector(
        "481c9e39b1",
        "d07cf6cbb7f313bdde66b727afd3c5e8",
        "8408dfff3c1a2b1292dc199e46b7d617",
        "33cce2eabff5a79d",
        "632a9d131ad4c168a4225d8e1ff755939974a7bede"),
    new EaxTestVector(
        "40d0c07da5e4",
        "35b6d0580005bbc12b0587124557d2c2",
        "fdb6b06676eedc5c61d74276e1f8e816",
        "aeb96eaebe2970e9",
        "071dfe16c675cb0677e536f73afe6a14b74ee49844dd"),
    new EaxTestVector(
        "4de3b35c3fc039245bd1fb7d",
        "bd8e6e11475e60b268784c38c62feb22",
        "6eac5c93072d8e8513f750935e46da1b",
        "d4482d1ca78dce0f",
        "835bb4f15d743e350e728414abb8644fd6ccb86947c5e10590210a4f"),
    new EaxTestVector(
        "8b0a79306c9ce7ed99dae4f87f8dd61636",
        "7c77d6e813bed5ac98baa417477a2e7d",
        "1a8c98dcd73d38393b2bf1569deefc19",
        "65d2017990d62528",
        "02083e3979da014812f59f11d52630da30137327d10649b0aa6e1c181db617d7" + "f2"),
    new EaxTestVector(
        "1bda122bce8a8dbaf1877d962b8592dd2d56",
        "5fff20cafab119ca2fc73549e20f5b0d",
        "dde59b97d722156d4d9aff2bc7559826",
        "54b9f04e6a09189a",
        "2ec47b2c4954a489afc7ba4897edcdae8cc33b60450599bd02c96382902aef7f" + "832a"),
    new EaxTestVector(
        "6cf36720872b8513f6eab1a8a44438d5ef11",
        "a4a4782bcffd3ec5e7ef6d8c34a56123",
        "b781fcf2f75fa5a8de97a9ca48e522ec",
        "899a175897561d7e",
        "0de18fd0fdd91e7af19f1d8ee8733938b1e8e7f6d2231618102fdb7fe55ff199" + "1700"),
    new EaxTestVector(
        "ca40d7446e545ffaed3bd12a740a659ffbbb3ceab7",
        "8395fcf1e95bebd697bd010bc766aac3",
        "22e7add93cfc6393c57ec0b3c17d6b44",
        "126735fcc320d25a",
        "cb8920f87a6c75cff39627b56e3ed197c552d295a7cfc46afc253b4652b1af37" + "95b124ab6e"),
    // The tests are self generated.
    //
    // Some test vectors for counter overflow:
    // Initial counter value == 2^128-1
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111",
        "000102030405060708090a0b0c0d0e0f",
        "3c8cc2970a008f75cc5beae2847258c2",
        "",
        "3c441f32ce07822364d7a2990e50bb13d7b02a26969e4a937e5e9073b0d9c968"
            + "db90bdb3da3d00afd0fc6a83551da95e"),
    // counter value overflows at 64-bit boundary
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111",
        "000102030405060708090a0b0c0d0e0f",
        "aef03d00598494e9fb03cd7d8b590866",
        "",
        "d19ac59849026a91aa1b9aec29b11a202a4d739fd86c28e3ae3d588ea21d70c6"
            + "c30f6cd9202074ed6e2a2a360eac8c47"),
    // no counter overflow, but the 64 most significant bits are set.
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111",
        "000102030405060708090a0b0c0d0e0f",
        "55d12511c696a80d0514d1ffba49cada",
        "",
        "2108558ac4b2c2d5cc66cea51d6210e046177a67631cd2dd8f09469733acb517"
            + "fc355e87a267be3ae3e44c0bf3f99b2b"),
    // counter value overflows at 32-bit boundary
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111",
        "000102030405060708090a0b0c0d0e0f",
        "79422ddd91c4eee2deaef1f968305304",
        "",
        "4d2c1524ca4baa4eefcce6b91b227ee83abaff8105dcafa2ab191f5df2575035"
            + "e2c865ce2d7abdac024c6f991a848390"),
    // no counter overflow, but bits 32-64 and 96-128 are set.
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111",
        "000102030405060708090a0b0c0d0e0f",
        "0af5aa7a7676e28306306bcd9bf2003a",
        "",
        "8eb01e62185d782eb9287a341a6862ac5257d6f9adc99ee0a24d9c22b3e9b38a"
            + "39c339bc8a74c75e2c65c6119544d61e"),
    // no counter overflow, lower 64 bits are 2^63-1
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111",
        "000102030405060708090a0b0c0d0e0f",
        "af5a03ae7edd73471bdcdfac5e194a60",
        "",
        "94c5d2aca6dbbce8c24513a25e095c0e54a942860d327a222a815cc713b163b4"
            + "f50b30304e45c9d411e8df4508a98612"),
    // counter overflow between block 2 and block 3.
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111"
            + "2222222222222222222222222222222233333333333333333333333333333333",
        "000102030405060708090a0b0c0d0e0f",
        "b37087680f0edd5a52228b8c7aaea664",
        "",
        "3bb6173e3772d4b62eef37f9ef0781f360b6c74be3bf6b371067bc1b090d9d66"
            + "22a1fbec6ac471b3349cd4277a101d40890fbf27dfdcd0b4e3781f9806daabb6"
            + "a0498745e59999ddc32d5b140241124e"),
    // no counter overflow, the lower 64 bits are 2^63-4.
    new EaxTestVector(
        "0000000000000000000000000000000011111111111111111111111111111111"
            + "2222222222222222222222222222222233333333333333333333333333333333"
            + "44444444444444444444444444444444",
        "000102030405060708090a0b0c0d0e0f",
        "4f802da62a384555a19bc2b382eb25af",
        "",
        "e9b0bb8857818ce3201c3690d21daa7f264fb8ee93cc7a4674ea2fc32bf182fb"
            + "2a7e8ad51507ad4f31cefc2356fe7936a7f6e19f95e88fdbf17620916d3a6f3d"
            + "01fc17d358672f777fd4099246e436e167910be744b8315ae0eb6124590c5d8b"),
    // 192-bit keys
    new EaxTestVector(
        "",
        "03dd258601c1d4872a52b27892db0356911b2df1436dc7f4",
        "723cb2022102113018dcd2d204022114",
        "",
        "c472b1c6c22b4f2b7e02409499aa2ade"),
    new EaxTestVector(
        "abcdef",
        "03dd258601c1d4872a52b27892db0356911b2df1436dc7f4",
        "025f3d2286c143976412022102696708231208",
        "8917328de211",
        "520f4f2cf1b893ae3ba8ecbac3a08ea57de2cd"),
    new EaxTestVector(
        "1111111111111111111111111111111122222222222222222222222222222222",
        "0172acf299142c001d0c231287c1182784554ca3a21908276ac2c92af1294612",
        "000102030405060708090a0b0c0d0e0f1a1b1c1d",
        "77922d34e452e0a40962873d22901dd22ad1c303",
        "5917879b9fa85f4007b7bd0cd46f067d5a7bf287f19dfcc5475c95a4acce520a"
            + "4c5df804bc091a3b5d6c838b7e494571"),
    // 256-bit keys
    new EaxTestVector(
        "",
        "0172acf299142c001d0c231287c1182784554ca3a21908276ac2c92af1294612",
        "696708231208",
        "",
        "7c8f86f837a4f72c574678d92f637f07"),
    new EaxTestVector(
        "abcdef",
        "0172acf299142c001d0c231287c1182784554ca3a21908276ac2c92af1294612",
        "696708231208",
        "8917328de211",
        "12486c87bf9a7f22fa65a9493ec0f57f8070f5"),
    new EaxTestVector(
        "1111111111111111111111111111111122222222222222222222222222222222",
        "0172acf299142c001d0c231287c1182784554ca3a21908276ac2c92af1294612",
        "000102030405060708090a0b0c0d0e0f1a1b1c1d",
        "92d3e42e0409273291d2dc034450",
        "5917879b9fa85f4007b7bd0cd46f067d5a7bf287f19dfcc5475c95a4acce520a"
            + "e632946e4999be20159977431bef0454"),
  };

  public void testEax() throws Exception {
    for (EaxTestVector test : EAX_TEST_VECTOR) {
      Cipher cipher = Cipher.getInstance("AES/EAX/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, test.key, test.parameters);
      cipher.updateAAD(test.aad);
      byte[] ct = cipher.doFinal(test.pt);
      assertEquals(test.ctHex, TestUtil.bytesToHex(ct));
    }
  }

  public void testLateUpdateAAD() throws Exception {
    for (EaxTestVector test : EAX_TEST_VECTOR) {
      Cipher cipher = Cipher.getInstance("AES/EAX/NoPadding");
      cipher.init(Cipher.ENCRYPT_MODE, test.key, test.parameters);
      byte[] c0 = cipher.update(test.pt);
      try {
        cipher.updateAAD(test.aad);
      } catch (java.lang.IllegalStateException ex) {
        // Typically one should pass the AAD in first.
        // Hence it is OK to get this exception.
        // For example, this is the behaviour of SUNJce.
        continue;
      }
      byte[] c1 = cipher.doFinal();
      String result = TestUtil.bytesToHex(c0) + TestUtil.bytesToHex(c1);
      assertEquals(test.ctHex, result);
    }
  }
}
