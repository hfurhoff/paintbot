export default interface IGameState {
    gameId: string,
    gameTick: number,
    map: IGameMap,
    receivingPlayerId: string,
    timeStamp: number,
    type: EventType
}

interface IGameMap {
    width: number,
    height: number,
    bombPositions: [number],
    obstaclePositions: [number],
    playerInfo: [IPlayerInfo],
    worldTick: number
}

interface IPlayerInfo {
    id: string,
    name: string,
    points: number,
    positions: [number],
    stunnedForGameTicks: number
}

enum EventType {
    MAP_UPDATE_EVENT = 'se.cygni.snake.api.event.MapUpdateEvent'
}