/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
 * 
 * This file is part of PlantUML.
 *
 * PlantUML is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlantUML distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * Original Author:  Arnaud Roques
 *
 * Revision $Revision: 9786 $
 *
 */
package net.sourceforge.plantuml.activitydiagram3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.sequencediagram.NotePosition;

public class InstructionSplit implements Instruction {

	private final List<InstructionList> splits = new ArrayList<InstructionList>();
	private final Instruction parent;
	private final LinkRendering inlinkRendering;

	public InstructionSplit(Instruction parent, LinkRendering inlinkRendering) {
		this.parent = parent;
		this.splits.add(new InstructionList());
		this.inlinkRendering = inlinkRendering;
	}

	private InstructionList getLast() {
		return splits.get(splits.size() - 1);
	}

	public void add(Instruction ins) {
		getLast().add(ins);
	}

	public Ftile createFtile(FtileFactory factory) {
		final List<Ftile> all = new ArrayList<Ftile>();
		for (InstructionList list : splits) {
			all.add(list.createFtile(factory));
		}
		return factory.createSplit(all);
	}

	public Instruction getParent() {
		return parent;
	}

	public void splitAgain(LinkRendering inlinkRendering) {
		if (inlinkRendering != null) {
			getLast().setOutRendering(inlinkRendering);
		}
		final InstructionList list = new InstructionList();
		this.splits.add(list);
	}

	public void endSplit(LinkRendering inlinkRendering) {
		if (inlinkRendering != null) {
			getLast().setOutRendering(inlinkRendering);
		}

	}

	final public boolean kill() {
		return getLast().kill();
	}

	public LinkRendering getInLinkRendering() {
		return inlinkRendering;
	}

	public boolean addNote(Display note, NotePosition position) {
		return getLast().addNote(note, position);
	}

	public Set<Swimlane> getSwimlanes() {
		return InstructionList.getSwimlanes2(splits);
	}

	public Swimlane getSwimlaneIn() {
		return parent.getSwimlaneOut();
	}

	public Swimlane getSwimlaneOut() {
		return getLast().getSwimlaneOut();
	}

}
