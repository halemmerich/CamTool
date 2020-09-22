package de.dieklaut.camtool.operations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import de.dieklaut.camtool.AbstractGroup;
import de.dieklaut.camtool.Constants;
import de.dieklaut.camtool.Context;
import de.dieklaut.camtool.DefaultSorter;
import de.dieklaut.camtool.FileBasedTest;
import de.dieklaut.camtool.Group;
import de.dieklaut.camtool.Sorter;
import de.dieklaut.camtool.TestFileHelper;
import de.dieklaut.camtool.renderjob.RenderJob;
import de.dieklaut.camtool.renderjob.RenderJobFactory;
import de.dieklaut.camtool.util.FileUtils;

public class RenderTest extends FileBasedTest {

	private static final String TEST = "test";

	@Test
	public void testPerform() throws IOException {
		Context context = Context.create(getTestFolder());
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));
		
		new Init().perform(context);
		Sorter sorter = new DefaultSorter();
		Sort sort = new Sort(sorter);
		sort.setName(TEST);
		sort.perform(context);
		
		RenderJobFactory.useRawtherapee = false;
		
		Render render = new Render(sorter);
		render.setSortingName(TEST);
		
		render.perform(context);
		
		String timestamp = FileUtils.getTimestamp(FileUtils.getCreationDate(source));
		
		Path results = getTestFolder().resolve(Constants.FOLDER_RESULTS);
		assertTrue(Files.exists(results));
		assertTrue(Files.exists(results.resolve(TEST)));
		assertTrue(Files.exists(results.resolve(TEST).resolve(timestamp + "_file.jpg")));
	}

	@Test
	public void testPerformTwice() throws IOException {
		Context context = Context.create(getTestFolder());
		
		Path source = TestFileHelper.getTestResource("A7II.ARW");
		Files.copy(source, getTestFolder().resolve("file.arw"));

		AtomicInteger renderJobCounter = new AtomicInteger(0);
		AtomicInteger storeCounter = new AtomicInteger(0);
		AtomicInteger predictCounter = new AtomicInteger(0);
		
		new Init().perform(context);
		Sorter sorter = new Sorter() {

			@Override
			public Collection<Group> identifyGroups(Path path) throws IOException {
				return wrapGroups(new DefaultSorter().identifyGroups(path));
			}

			private Collection<Group> wrapGroups(Collection<Group> identifyGroups) {
				Collection<Group> groups = new HashSet<>();
				for (Group group : identifyGroups) {
					groups.add(new AbstractGroup() {

						@Override
						public Collection<Path> getAllFiles() {
							return group.getAllFiles();
						}

						@Override
						public RenderJob getRenderJob() {
							renderJobCounter.incrementAndGet();
							RenderJob job = group.getRenderJob();
							return new RenderJob() {

								@Override
								public Set<Path> storeImpl(Path destination) throws IOException {
									storeCounter.incrementAndGet();
									return job.storeImpl(destination);
								}

								@Override
								public Set<Path> getPredictedResultsImpl(Path destination) throws IOException {
									predictCounter.incrementAndGet();
									return job.getPredictedResultsImpl(destination);
								}
								
							};
						}

						@Override
						public boolean isMarkedAsDeleted() {
							return group.isMarkedAsDeleted();
						}

						@Override
						public boolean hasOwnFolder() {
							return group.hasOwnFolder();
						}

						@Override
						public Path getContainingFolder() {
							return group.getContainingFolder();
						}

						@Override
						public String getType() {
							return group.getType();
						}

						@Override
						public void moveToFolder(Path destination) {
							group.moveToFolder(destination);
						}

						@Override
						public Instant getTimestamp() {
							return group.getTimestamp();
						}

						@Override
						public Duration getDuration() {
							return group.getDuration();
						}

						@Override
						public String getName() {
							return group.getName();
						}

						@Override
						public String getCreator() {
							return group.getCreator();
						}

					});
				}
				return groups;
			}
			
		};
		Sort sort = new Sort(sorter);
		sort.setName(TEST);
		sort.perform(context);
		
		RenderJobFactory.useRawtherapee = false;
		
		//First "normal" rendering
		Render render = new Render(sorter);
		render.setSortingName(TEST);
		
		render.perform(context);

		assertEquals(1, renderJobCounter.get());
		assertEquals(1, storeCounter.get());
		assertEquals(1, predictCounter.get());
		
		//Rendering again, without changes
		render = new Render(sorter);
		render.setSortingName(TEST);
		
		render.perform(context);
		
		assertEquals(2, renderJobCounter.get());
		assertEquals(1, storeCounter.get());
		assertEquals(2, predictCounter.get());
		
		//Rendering again, without changes and a file to clear from full size results
		Path toBeDeleted = Files.createFile(getTestFolder().resolve(Constants.FOLDER_RESULTS).resolve(TEST).resolve("20200101120000000_fileasdf.jpg"));
		render = new Render(sorter);
		render.setSortingName(TEST);
		
		render.perform(context);
		
		assertFalse(Files.exists(toBeDeleted));
		assertEquals(3, renderJobCounter.get());
		assertEquals(1, storeCounter.get());
		assertEquals(3, predictCounter.get());
		
		//Rendering with additional pp3 file
		Files.createFile(context.getRoot().resolve(Constants.FOLDER_SORTED).resolve(TEST).resolve(FileUtils.getTimestamp(source) + "_file.arw.pp3"));

		render = new Render(sorter);
		render.setSortingName(TEST);
		
		render.perform(context);
		
		assertEquals(4, renderJobCounter.get());
		assertEquals(2, storeCounter.get());
		assertEquals(4, predictCounter.get());
	}

	@Test
	public void testPerformOneGroup() throws IOException {
		Context context = TestFileHelper.createComplexContext(getTestFolder());
		Path file1 = TestFileHelper.addFileToSorting(context, Paths.get("group/file1.ARW"), TestFileHelper.getTestResource("A7II.ARW"));
		Path file2 = TestFileHelper.addFileToSorting(context, Paths.get("file2.ARW"), TestFileHelper.getTestResource("A7II.ARW"));

		Sorter sorter = new DefaultSorter();		
		RenderJobFactory.useRawtherapee = false;
		
		Render render = new Render(sorter);
		render.setSortingName(Constants.DEFAULT_SORTING_NAME);
		render.setNameOfGroup("group");
		render.perform(context);
		
		Path results = getTestFolder().resolve(Constants.FOLDER_RESULTS);
		assertTrue(Files.exists(results));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME)));
		assertTrue(Files.exists(results.resolve(Constants.DEFAULT_SORTING_NAME).resolve(FileUtils.getTimestamp(file1) + "_file1.jpg")));
	}
}
