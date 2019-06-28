package com.kazurayam.katalon.download

import static org.hamcrest.CoreMatchers.*
import static org.hamcrest.MatcherAssert.*
import static org.junit.Assert.*

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import org.junit.BeforeClass
import org.junit.Test
import org.junit.Ignore
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.kazurayam.junit4ks.IgnoreRestSupportRunner
import com.kazurayam.junit4ks.IgnoreRest
import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.network.ProxyInformation
import com.kms.katalon.core.testobject.RequestObject

import internal.GlobalVariable

@RunWith(IgnoreRestSupportRunner.class)
class DownloaderClientTest {

	private static Path testOutputDir_

	@BeforeClass public static void onlyOnce() {
		Path projectDir = Paths.get(RunConfiguration.getProjectDir())
		testOutputDir_ = projectDir.resolve(Paths.get('build/tmp/testOutput/DownloadingClientTest'))
	}

	@Test
	void test_send() {
		// setup:
		Path caseOutputDir = testOutputDir_.resolve(Paths.get('test_send'))
		Files.createDirectories(caseOutputDir)
		Path outFile = caseOutputDir.resolve('output.pdf')
		OutputStream out = new FileOutputStream(outFile.toFile())
		//
		Map data = (Map)GlobalVariable.JMA_RAIN_DATA_URL
		String url = data.get('1h')

		// when:
		ProxyInformation proxyInformation = RunConfiguration.getProxyInformation()
		DownloaderClient client = new DownloaderClient('DownloaderClientTest', proxyInformation)
		RequestObject request = new RequestObject()
		request.setRestRequestMethod('GET')
		request.setRestUrl(url)
		DownloaderClient.StreamingResponseObject response = client.send(request)

		response.getInputStream().with { is ->
			try {
				byte[] buffer = new byte[1024]
				int len = 0
				while (true) {
					len = is.read(buffer)

					throw new RuntimeException("blocked at the above line")

					if (len == -1) {
						break
					}
					out.write(buffer, 0, len)

				}

			} finally {
				is.close()
				out.flush()
				out.close()
			}
		}
		// then:
		assertThat(outFile.toString() + " should exist", Files.exists(outFile), is(equalTo(true)))
		assertTrue(outFile.toString() + " should not be empty", Files.size(outFile) > 0)
	}
}