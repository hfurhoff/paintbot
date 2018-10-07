import * as React from 'react';
import './App.css';

import GameBoard from './gameboard/GameBoard';

class App extends React.Component {
  public render() {
    return (
      <div className="App">
        <header className="App-header">
          <h1 className="App-title">Welcome to React</h1>
        </header>
          <GameBoard />
      </div>
    );
  }
}

export default App;
