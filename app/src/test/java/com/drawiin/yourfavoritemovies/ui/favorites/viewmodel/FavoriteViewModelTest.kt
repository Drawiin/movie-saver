import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileInfo
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileUid
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.drawiin.yourfavoritemovies.ui.favorites.viewmodel.FavoriteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class FavoriteViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @MockK
    lateinit var auth: FirebaseAuth

    @MockK
    lateinit var database: DatabaseReference

    @MockK
    lateinit var getCurrentProfileUid: GetCurrentProfileUid

    @MockK
    lateinit var getCurrentProfileInfo: GetCurrentProfileInfo

    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = FavoriteViewModel(auth, database, getCurrentProfileUid, getCurrentProfileInfo)
    }

    @Test
    fun `loadWatchList updates stateList with profile watchList`() {
        val mockProfile = Profile(watchList = listOf(Movie(id = 1), Movie(id = 2)))
        every { getCurrentProfileInfo.run(any()) } answers { mockProfile }

        val observer = mockk<Observer<List<Movie>>>(relaxed = true)
        viewModel.stateList.observeForever(observer)

        viewModel.loadWatchList()

        viewModel.stateList.value?.let {
            assert(it == mockProfile.watchList)
        }
    }

    @Test
    fun `moveToWatchedMovies with movie not in watched adds to watched`(): Unit = runBlocking {
        val movieToMove = Movie(id = 1)
        val initialWatchedMovies = listOf<Movie>()
        val initialWatchList = listOf(movieToMove)
        val mockProfile = Profile(watchedMovies = initialWatchedMovies, watchList = initialWatchList)
        every { getCurrentProfileInfo.run(any()) } answers { mockProfile }

        val observer = mockk<Observer<Movie>>(relaxed = true)
        viewModel.stateMovedToWatchedMovies.observeForever(observer)

        viewModel.moveToWatchedMovies(movieToMove)

        viewModel.stateMovedToWatchedMovies.value?.let {
            assert(it == movieToMove)
        }
    }

    @Test
    fun `moveToWatchedMovies with movie already in watched does not add to watched`() {
        val movieToMove = Movie(id = 1)
        val initialWatchedMovies = listOf(movieToMove)
        val initialWatchList = listOf<Movie>()
        val mockProfile = Profile(watchedMovies = initialWatchedMovies, watchList = initialWatchList)
        every { getCurrentProfileInfo.run(any()) } answers { mockProfile }

        val observer = mockk<Observer<Movie>>(relaxed = true)
        viewModel.stateMovedToWatchedMovies.observeForever(observer)

        viewModel.moveToWatchedMovies(movieToMove)

        verify(exactly = 0) { observer.onChanged(any()) }
    }
}