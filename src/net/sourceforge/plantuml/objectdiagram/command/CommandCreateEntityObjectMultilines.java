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
 * Revision $Revision: 4161 $
 *
 */
package net.sourceforge.plantuml.objectdiagram.command;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.objectdiagram.AbstractClassOrObjectDiagram;
import net.sourceforge.plantuml.objectdiagram.ObjectDiagram;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class CommandCreateEntityObjectMultilines extends CommandMultilines2<AbstractClassOrObjectDiagram> {

	public CommandCreateEntityObjectMultilines() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE);
	}

	private static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("TYPE", "(object)[%s]+"), //
				new RegexLeaf("NAME", "(?:[%g]([^%g]+)[%g][%s]+as[%s]+)?([\\p{L}0-9_.]+)"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
				new RegexLeaf("[%s]*"), //
				new RegexLeaf("URL", "(" + UrlBuilder.getRegexp() + ")?"), //
				new RegexLeaf("[%s]*"), //
				ColorParser.exp1(), //
				new RegexLeaf("[%s]*\\{[%s]*$"));
	}

	@Override
	public String getPatternEnd() {
		return "(?i)^[%s]*\\}[%s]*$";
	}

	public CommandExecutionResult executeNow(AbstractClassOrObjectDiagram diagram, BlocLines lines) {
		lines = lines.trim(true);
		final RegexResult line0 = getStartingPattern().matcher(StringUtils.trin(lines.getFirst499()));
		final IEntity entity = executeArg0(diagram, line0);
		if (entity == null) {
			return CommandExecutionResult.error("No such entity");
		}
		lines = lines.subExtract(1, 1);
		for (CharSequence s : lines) {
			assert s.length() > 0;
			if (VisibilityModifier.isVisibilityCharacter(s.charAt(0))) {
				diagram.setVisibilityModifierPresent(true);
			}
			entity.getBodier().addFieldOrMethod(s.toString());
		}
		return CommandExecutionResult.ok();
	}

	private IEntity executeArg0(AbstractClassOrObjectDiagram diagram, RegexResult line0) {
		final Code code = Code.of(line0.get("NAME", 1));
		final String display = line0.get("NAME", 0);
		final String stereotype = line0.get("STEREO", 0);
		if (diagram.leafExist(code)) {
			return diagram.getOrCreateLeaf(code, LeafType.OBJECT, null);
		}
		final IEntity entity = diagram.createLeaf(code, Display.getWithNewlines(display), LeafType.OBJECT, null);
		if (stereotype != null) {
			entity.setStereotype(new Stereotype(stereotype, diagram.getSkinParam().getCircledCharacterRadius(), diagram
					.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER), diagram.getSkinParam()
					.getIHtmlColorSet()));
		}
		entity.setSpecificColorTOBEREMOVED(ColorType.BACK, diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(line0.get("COLOR", 0)));
		return entity;
	}

}
