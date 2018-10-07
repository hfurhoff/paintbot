import axios from 'axios';
import * as React from 'react';
import IGameState from '../gamestate/gamestate'
export default class GameBoard extends React.Component<{}, {gameState: IGameState}> {
    constructor(props: any) {
        super(props);

        this.state = {
            gameState: {} as IGameState
        }
    }

    public componentDidMount () {
        axios.get('http://localhost:8080/states')
            .then( response => {
                const state = response.data as IGameState;
                this.setState({gameState: state});
            });
    }

    public render() {
        return (
            <div>Tjenare {this.state.gameState.gameId} </div>
        )
    }
}