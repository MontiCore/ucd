/* (c) https://github.com/MontiCore/monticore */
package ucd._ast;

import java.util.Objects;

public class UCDEdge {
  private String from;
  private String to;

  public UCDEdge(String from, String to) {
    this.from = from;
    this.to = to;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    UCDEdge ucdEdge = (UCDEdge) o;
    return getFrom().equals(ucdEdge.getFrom()) && getTo().equals(ucdEdge.getTo());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getFrom(), getTo());
  }
}
