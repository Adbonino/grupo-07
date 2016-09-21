package model.state;

public interface IState {

    String getID();

    String getDescription();

    boolean equals(IState state);

    void modify(String newState);

}