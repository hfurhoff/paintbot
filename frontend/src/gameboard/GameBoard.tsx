import axios from 'axios';
import * as React from 'react';
import IGameState from '../gamestate/gamestate'
import Blob from './Blob'

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
            <div>
                <Blob color={"#2196f3"} rotation={"90deg"}/>
                <Blob color={"#f44336"} rotation={"0deg"}/>
                <Blob color={"#ffeb3b"} rotation={"270deg"}/>
                <Blob color={"#4caf50"} rotation={"180deg"}/>
            </div>
        )
    }
}