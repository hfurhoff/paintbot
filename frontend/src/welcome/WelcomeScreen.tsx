import React from 'react';
import { RouteComponentProps } from 'react-router-dom';
import styled from 'styled-components';

import { StandardColors } from '../common/Constants';
import { DefaultButton } from '../common/ui/DefaultButton';
import { Indent } from '../common/ui/Indent';
import { Row } from '../common/ui/Row';
import { Spacing } from '../common/ui/Spacing';
import background from '../resources/background.jpg';

import introImage from './images/welcome.png';
import yellowCharacter from './images/yellow_character.png';

export default class WelcomeScreen extends React.Component<RouteComponentProps> {
  private readonly startGame = () => {
    this.props.history.push('/game');
  };

  render() {
    return (
      <Container>
        <MenuContainer>
          <Row justifyContent={'space-between'}>
            <Spacing>
              <Row>
                <HeaderText>FÄRGLANDET</HeaderText>
                <YellowCharacter src={yellowCharacter} />
              </Row>
            </Spacing>
            <Spacing>
              <Indent num={2}>
                <DefaultButton onClick={this.startGame}>Start</DefaultButton>
              </Indent>
            </Spacing>
          </Row>
        </MenuContainer>
        <BodyContainer>
          <ComicImage src={introImage} />
        </BodyContainer>
      </Container>
    );
  }
}

const Container = styled.div`
  background-image: url(${background});
  position: absolute;
  background-repeat: no-repeat;
  background-size: cover;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
`;

const MenuContainer = styled.div`
  line-height: 50px;
  background-color: rgba(0, 0, 0, 0.1);
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
`;

const HeaderText = styled.span`
  font-size: 30px;
  font-weight: bold;
  height: 100%;
  padding-left: 20px;
  color: ${StandardColors.White};
`;

const BodyContainer = styled.div`
  margin-top: 20px;
  display: flex;
  justify-content: center;
`;

const ComicImage = styled.img`
  width: 70%;
  height: 70%;
`;

const YellowCharacter = styled.img`
  width: 30px;
  height: 30px;
`;
