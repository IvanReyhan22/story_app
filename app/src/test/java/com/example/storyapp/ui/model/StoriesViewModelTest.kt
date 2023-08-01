package com.example.storyapp.ui.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.example.storyapp.DataDummy
import com.example.storyapp.MainDispatcherRule
import com.example.storyapp.adapter.ListStoriesAdapter
import com.example.storyapp.data.StoryRepositories
import com.example.storyapp.data.local.entity.StoriesEntity
import com.example.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.internal.matchers.Null
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
class StoriesViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepositories: StoryRepositories

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `get Stories Data and Should Not Null`() = runTest {
        val dummyStories = DataDummy.generateDummyQuoteResponse()
        val data: PagingData<StoriesEntity> = PagingData.from(dummyStories)
        val expectedStories = MutableLiveData<PagingData<StoriesEntity>>()
        expectedStories.value = data

        Mockito.`when`(storyRepositories.getStories()).thenReturn(expectedStories)

        val application = RuntimeEnvironment.getApplication()
        val viewModel = StoriesViewModel(application, storyRepositories)

        val actualStories: PagingData<StoriesEntity> = viewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }

    @Test
    fun `get Empty Stories Data and Return null`() = runTest {
        val data: PagingData<StoriesEntity> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<StoriesEntity>>()
        expectedStories.value = data

        Mockito.`when`(storyRepositories.getStories()).thenReturn(expectedStories)

        val application = RuntimeEnvironment.getApplication()
        val viewModel = StoriesViewModel(application, storyRepositories)

        val actualStories: PagingData<StoriesEntity> = viewModel.stories.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ListStoriesAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualStories)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(0,differ.snapshot().size)
    }

}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}