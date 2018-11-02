import React from 'react';

import { GameControllerColors, StandardColors } from '../../common/Constants';
import { DefaultButton } from '../../common/ui/DefaultButton';

interface Props {
  playing: boolean;
  onClick: () => void;
}

export const PlayControllButton = ({ playing, onClick }: Props) => {
  const playStatusText = playing ? 'Pause' : 'Play';
  return (
    <DefaultButton
      onClick={onClick}
      width={200}
      color={StandardColors.White}
      backgroundColor={GameControllerColors.PlayButton}
    >
      {playStatusText}
    </DefaultButton>
  );
};
