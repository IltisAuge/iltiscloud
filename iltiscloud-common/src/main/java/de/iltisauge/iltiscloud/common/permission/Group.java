package de.iltisauge.iltiscloud.common.permission;

import de.iltisauge.iltiscloud.api.permission.IGroup;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Group implements IGroup {

	private final String name;
	private final String displayName;
	private final Integer sortingIndex;
	
	@Override
	public int compareTo(IGroup o) {
		return sortingIndex.compareTo(o.getSortingIndex());
	}
}
