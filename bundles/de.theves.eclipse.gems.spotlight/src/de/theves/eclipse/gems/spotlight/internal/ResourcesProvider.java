package de.theves.eclipse.gems.spotlight.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.model.IWorkbenchAdapter;

public class ResourcesProvider implements SpotlightItemProvider {

	@Override
	public List<SpotlightItem> getItems() {
		List<SpotlightItem> items = new ArrayList<>();
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceVisitor() {
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getType() == IResource.FILE) {
						items.add(new ResourceItem(ResourcesProvider.this, (IFile) resource));
					}
					return resource.getType() != IResource.FILE;
				}
			});
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return items;
	}

	IWorkbenchAdapter getAdapter(IResource res) {
		return res.getAdapter(IWorkbenchAdapter.class);
	}

	@Override
	public String getLabel() {
		return "Resources";
	}

}