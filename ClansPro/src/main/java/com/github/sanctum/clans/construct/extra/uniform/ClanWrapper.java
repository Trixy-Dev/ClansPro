package com.github.sanctum.clans.construct.extra.uniform;

import com.github.sanctum.clans.construct.api.Clan;
import com.github.sanctum.clans.construct.api.ClansAPI;
import com.github.sanctum.labyrinth.formatting.UniformedComponents;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClanWrapper extends UniformedComponents<Clan> implements Serializable {

	private static final long serialVersionUID = -5063742983811634219L;
	private final Clan clan;
	private final RelationType relation;

	public ClanWrapper(Clan clan, RelationType type) {
		this.clan = clan;
		this.relation = type;
	}

	@Override
	public List<Clan> list() {
		switch (relation) {
			case Ally:
				return clan.getAllyList().stream().map(s -> ClansAPI.getInstance().getClan(s)).collect(Collectors.toList());
			case Enemy:
				return clan.getEnemyList().stream().map(s -> ClansAPI.getInstance().getClan(s)).collect(Collectors.toList());
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public List<Clan> sort() {
		list().sort(Comparator.comparingDouble(Clan::getPower));
		switch (relation) {
			case Ally:
			case Enemy:
				return list();
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public List<Clan> sort(Comparator<? super Clan> comparable) {
		list().sort(comparable);
		switch (relation) {
			case Ally:
			case Enemy:
				return list();
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public Collection<Clan> collect() {
		switch (relation) {
			case Ally:
			case Enemy:
				return list();
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public Clan[] array() {
		switch (relation) {
			case Ally:
			case Enemy:
				return list().toArray(new Clan[0]);
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public <R> Stream<R> map(Function<? super Clan, ? extends R> mapper) {
		switch (relation) {
			case Ally:
			case Enemy:
				return list().stream().map(mapper);
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public Stream<Clan> filter(Predicate<? super Clan> predicate) {
		switch (relation) {
			case Ally:
			case Enemy:
				return list().stream().filter(predicate);
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public Clan getFirst() {
		switch (relation) {
			case Ally:
			case Enemy:
				return list().get(0);
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public Clan getLast() {
		switch (relation) {
			case Ally:
			case Enemy:
				return list().get(Math.max(list().size() - 1, 0));
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	@Override
	public Clan get(int index) {
		switch (relation) {
			case Ally:
			case Enemy:
				return list().get(index);
			default:
				throw new IllegalStateException("Invalid relation type!");
		}
	}

	public enum RelationType {
		Ally, Enemy
	}


}
