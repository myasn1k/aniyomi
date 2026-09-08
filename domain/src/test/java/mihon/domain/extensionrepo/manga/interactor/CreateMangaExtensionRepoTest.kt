package mihon.domain.extensionrepo.manga.interactor

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import mihon.domain.extensionrepo.manga.repository.MangaExtensionRepoRepository
import mihon.domain.extensionrepo.model.ExtensionRepo
import mihon.domain.extensionrepo.service.ExtensionRepoMetaDto
import mihon.domain.extensionrepo.service.ExtensionRepoService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CreateMangaExtensionRepoTest {

    private val repository = mockk<MangaExtensionRepoRepository>(relaxed = true)
    private val service = mockk<ExtensionRepoService>()
    private val interactor = CreateMangaExtensionRepo(repository, service)

    @ParameterizedTest
    @ValueSource(strings = ["index.pb", "index.min.json"])
    fun `protobuf and legacy JSON links resolve the repository base URL`(index: String) = runTest {
        val baseUrl = "https://raw.githubusercontent.com/keiyoushi/extensions/repo"
        val repo = ExtensionRepo(baseUrl, "Example", null, "https://example.org", "fingerprint")
        coEvery { service.fetchRepoDetails(baseUrl) } returns repo

        assertEquals(CreateMangaExtensionRepo.Result.Success, interactor.await("$baseUrl/$index"))

        coVerify(exactly = 1) { service.fetchRepoDetails(baseUrl) }
        coVerify(exactly = 1) {
            repository.insertRepo(baseUrl, repo.name, null, repo.website, repo.signingKeyFingerprint)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["http://example.org/index.pb", "https://example.org/repo.json", "not a URL"])
    fun `invalid links are rejected without fetching or saving`(url: String) = runTest {
        assertEquals(CreateMangaExtensionRepo.Result.InvalidUrl, interactor.await(url))
        coVerify(exactly = 0) { service.fetchRepoDetails(any()) }
        coVerify(exactly = 0) { repository.insertRepo(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `repository metadata may omit the optional short name`() {
        val metadata = Json { ignoreUnknownKeys = true }.decodeFromString<ExtensionRepoMetaDto>(
            """
            {
                "index_v2": "https://example.org/index.pb",
                "meta": {
                    "name": "Example",
                    "website": "https://example.org",
                    "signingKeyFingerprint": "fingerprint"
                }
            }
            """.trimIndent(),
        )

        assertEquals("Example", metadata.meta.name)
        assertNull(metadata.meta.shortName)
    }
}
