package de.theves.eclipse.gems.spotlight.internal.providers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandImageService;
import org.eclipse.ui.commands.ICommandService;

import de.theves.eclipse.gems.spotlight.internal.view.SpotlightItem;
import de.theves.eclipse.gems.spotlight.internal.view.SpotlightItemProvider;
import de.theves.eclipse.gems.spotlight.internal.view.SpotlightItemsFilter;

public class CommandProvider implements SpotlightItemProvider {
	private IWorkbenchWindow window;

	public CommandProvider(IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public int getCategory() {
		return CATEGORY_COMMANDS;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<SpotlightItem<?>> getItems(SpotlightItemsFilter filter, IProgressMonitor monitor) {
		try {
			List<SpotlightItem<?>> items = new ArrayList<>();
			ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);
			final Collection commandIds = commandService.getDefinedCommandIds();
			monitor.beginTask(null, commandIds.size());
			final Iterator commandIdItr = commandIds.iterator();
			while (commandIdItr.hasNext()) {
				final String currentCommandId = (String) commandIdItr.next();
				final Command command = commandService.getCommand(currentCommandId);
				if (command != null) {
					try {
						Collection combinations = ParameterizedCommand.generateCombinations(command);
						for (Iterator it = combinations.iterator(); it.hasNext();) {
							ParameterizedCommand pc = (ParameterizedCommand) it.next();
							if (pc.getCommand().isDefined() && pc.getCommand().isEnabled()) {
								// only add commands that can be executed
								items.add(new CommandItem(this, pc));
							}
						}
					} catch (final NotDefinedException e) {
						// we do not add undefined commands because they are
						// invalid
					}
				}
				monitor.worked(1);
			}
			return items;
		} finally {
			monitor.done();
		}
	}

	public ICommandImageService getCommandImageService() {
		return this.window.getService(ICommandImageService.class);
	}

	public IWorkbenchWindow getWindow() {
		return window;
	}

	@Override
	public String getLabel() {
		return "Commands";
	}

}
