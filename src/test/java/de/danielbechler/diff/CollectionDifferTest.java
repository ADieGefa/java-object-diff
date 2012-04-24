/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.danielbechler.diff;

import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import org.junit.*;

import java.util.*;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;

/** @author Daniel Bechler */
public class CollectionDifferTest
{
	private CollectionDiffer differ;

	@Before
	public void setUp() throws Exception
	{
		differ = new CollectionDiffer();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructionWithoutDelegate()
	{
		new CollectionDiffer(null);
	}

	@Test
	public void testConstructionWithDelegate()
	{
		new CollectionDiffer(new DelegatingObjectDifferImpl());
	}

	@Test
	public void testCompareWithEmptyLists()
	{
		final List<String> working = Collections.emptyList();
		final List<String> base = Collections.emptyList();
		final CollectionNode node = differ.compare(working, base);
		assertThat(node.hasChanges(), is(false));
		assertThat(node.hasChildren(), is(false));
	}

	@Test
	public void testCompareWithAddedCollection() throws Exception
	{
		final List<Object> working = Collections.emptyList();
		final CollectionNode node = differ.compare(working, null);
		assertThat(node.getState(), is(Node.State.ADDED));
	}

	@Test
	public void testCompareWithRemovedCollection() throws Exception
	{
		final List<Object> working = null;
		final List<Object> base = Collections.emptyList();
		final CollectionNode node = differ.compare(working, base);
		assertThat(node.getState(), is(Node.State.REMOVED));
	}

	@Test
	public void testCompareWithAddedItem() throws Exception
	{
		final Collection<String> working = new LinkedList<String>(Arrays.asList("foo"));
		final Collection<String> base = new LinkedList<String>();

		final CollectionNode node = differ.compare(working, base);

		assertThat(node.hasChanges(), is(true));

		final Node child = node.getChild(new PropertyPathBuilder().withRoot().withCollectionItem("foo").build());
		assertThat(child.getState(), is(Node.State.ADDED));
	}

	@Test
	public void testCompareWithRemovedItem() throws Exception
	{
		final Collection<String> working = new LinkedList<String>();
		final Collection<String> base = new LinkedList<String>(Arrays.asList("foo"));

		final CollectionNode node = differ.compare(working, base);

		assertThat(node.hasChanges(), is(true));

		final Node child = node.getChild(new PropertyPathBuilder().withRoot().withCollectionItem("foo").build());
		assertThat(child.getState(), is(Node.State.REMOVED));
	}

	@Test
	public void testCompareWithChangedItem() throws Exception
	{
		final List<ObjectWithHashCodeAndEquals> working = Arrays.asList(new ObjectWithHashCodeAndEquals("foo", "1"));
		final List<ObjectWithHashCodeAndEquals> base = Arrays.asList(new ObjectWithHashCodeAndEquals("foo", "2"));

		final CollectionNode node = differ.compare(working, base);

		assertThat(node.hasChanges(), is(true));

		final PropertyPath propertyPath = new PropertyPathBuilder()
				.withRoot()
				.withCollectionItem(new ObjectWithHashCodeAndEquals("foo"))
				.build();
		final Node child = node.getChild(propertyPath);
		assertThat(child.getState(), is(Node.State.CHANGED));
	}
}
