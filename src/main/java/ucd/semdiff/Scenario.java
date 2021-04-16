package ucd.semdiff;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Scenario {
  private final Set<String> val;
  private final Set<String> ucs;
  private final Multimap<String, String> actor2uc;

  public Scenario(Set<String> val, Set<String> ucs, Multimap<String, String> actor2uc) {
    this.val = val;
    this.ucs = new HashSet<>(ucs);
    this.actor2uc = HashMultimap.create(actor2uc);
  }

  public Set<String> getVal() {
    return val;
  }

  public Set<String> getUcs() {
    return ucs;
  }

  public Multimap<String, String> getActor2uc() {
    return actor2uc;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Scenario scenario = (Scenario) o;
    return getVal().equals(scenario.getVal()) && getUcs().equals(scenario.getUcs()) && getActor2uc().equals(scenario.getActor2uc());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getVal(), getUcs(), getActor2uc());
  }

  /**
   * Returns set containing all linked actors.
   *
   * @return set of actors
   */
  public Set<String> getLinkedActors() {
    return new HashSet<>();
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder();
    s.append("scenario {");
    s.append("  Statisfied variables: " + val + System.lineSeparator());
    s.append("  Use cases: " + ucs + System.lineSeparator());
    for (String actor : actor2uc.keySet()) {
      s.append("  @" + actor + "--");
      s.append(actor2uc.get(actor) + System.lineSeparator());
    }
    s.append("}");
    return s.toString();
  }
}
