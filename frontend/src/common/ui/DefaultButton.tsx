import React from 'react';
import styled from 'styled-components';

import { CharacterColors, StandardColors } from '../Constants';

interface Props {
  color?: string;
  backgroundColor?: string;
  children?: string;
  onClick?: () => void;
  width?: number;
}

interface ButtonProps {
  color?: string;
  backgroundColor?: string;
  width?: number;
}

const Span = styled.span`
  cursor: pointer;
  display: inline-block;
  position: relative;
`;

const Button = styled.button<ButtonProps>`
  display: inline-block;
  border-radius: 4px;
  background-color: ${props => props.backgroundColor || CharacterColors.RedCharacter};
  border: none;
  color: ${props => props.color || StandardColors.White};
  text-align: center;
  font-size: 18px;
  padding: 10px;
  width: ${props => props.width}px;
`;

export const DefaultButton = ({ color, backgroundColor, children, onClick, width }: Props) => {
  return (
    <Button onClick={onClick} width={width} color={color} backgroundColor={backgroundColor}>
      <Span>{children}</Span>
    </Button>
  );
};
