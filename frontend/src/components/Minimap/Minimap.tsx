import React, { useMemo } from 'react';
import { Stage, Layer, Line, Rect } from 'react-konva';
import { Line as LineType } from '../../types/drawing';
import './Minimap.css';

interface MinimapProps {
    lines: LineType[];
    viewportDimensions: { width: number; height: number };
    scale: number;
    panOffset: { x: number; y: number };
    setScale: (scale: number) => void;
    setPanOffset: (offset: { x: number; y: number }) => void;
    boardBackgroundColor?: string;
}

const MINIMAP_MAX_SIZE = 200;

export const Minimap: React.FC<MinimapProps> = ({
    lines,
    viewportDimensions,
    scale,
    panOffset,
    setScale,
    setPanOffset,
    boardBackgroundColor = '#ffffff',
}) => {
    const bounds = useMemo(() => {
        let minX = Infinity, minY = Infinity;
        let maxX = -Infinity, maxY = -Infinity;

        if (lines.length > 0) {
            lines.forEach((line) => {
                if (!line.points || line.points.length === 0) return;

                line.points.forEach((point: any, index: number, arr: any[]) => {
                    let x: number, y: number;

                    if (typeof point === 'object' && point !== null && 'x' in point) {
                        x = point.x;
                        y = point.y;
                    }
                    else if (typeof point === 'number') {
                        if (index % 2 !== 0) return;
                        x = point;
                        y = arr[index + 1];
                    } else {
                        return;
                    }

                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                });
            });
        }

        if (minX === Infinity) {
            minX = -panOffset.x / scale;
            minY = -panOffset.y / scale;
            maxX = (-panOffset.x + viewportDimensions.width) / scale;
            maxY = (-panOffset.y + viewportDimensions.height) / scale;
        }

        const paddingX = Math.max((maxX - minX) * 0.1, viewportDimensions.width / scale * 0.5);
        const paddingY = Math.max((maxY - minY) * 0.1, viewportDimensions.height / scale * 0.5);

        return {
            x: minX - paddingX,
            y: minY - paddingY,
            width: (maxX - minX) + paddingX * 2,
            height: (maxY - minY) + paddingY * 2,
        };
    }, [lines, panOffset, scale, viewportDimensions]);

    const minimapScale = Math.min(
        MINIMAP_MAX_SIZE / bounds.width,
        MINIMAP_MAX_SIZE / bounds.height
    );

    const mapWidth = bounds.width * minimapScale;
    const mapHeight = bounds.height * minimapScale;

    const viewWidth = (viewportDimensions.width / scale) * minimapScale;
    const viewHeight = (viewportDimensions.height / scale) * minimapScale;

    const viewX = (-panOffset.x / scale - bounds.x) * minimapScale;
    const viewY = (-panOffset.y / scale - bounds.y) * minimapScale;

    const handleMapClick = (e: any) => {
        if (e.target.name() === 'viewbox') return;

        const stage = e.target.getStage();
        const pos = stage.getPointerPosition();
        if (!pos) return;

        const targetCanvasX = (pos.x / minimapScale) + bounds.x;
        const targetCanvasY = (pos.y / minimapScale) + bounds.y;

        setPanOffset({
            x: viewportDimensions.width / 2 - targetCanvasX * scale,
            y: viewportDimensions.height / 2 - targetCanvasY * scale,
        });
    };

    const handleDragMove = (e: any) => {
        const newViewX = e.target.x();
        const newViewY = e.target.y();

        const newCanvasX = (newViewX / minimapScale) + bounds.x;
        const newCanvasY = (newViewY / minimapScale) + bounds.y;

        setPanOffset({
            x: -newCanvasX * scale,
            y: -newCanvasY * scale,
        });
    };

    const handleWheel = (e: any) => {
        e.evt.preventDefault();

        const direction = e.evt.deltaY > 0 ? -1 : 1;
        const scaleBy = 1.1;
        const newScale = direction > 0 ? scale * scaleBy : scale / scaleBy;
        const clampedScale = Math.max(0.1, Math.min(newScale, 10));

        const centerX = viewportDimensions.width / 2;
        const centerY = viewportDimensions.height / 2;
        const centerLayerX = (centerX - panOffset.x) / scale;
        const centerLayerY = (centerY - panOffset.y) / scale;

        setPanOffset({
            x: centerX - centerLayerX * clampedScale,
            y: centerY - centerLayerY * clampedScale,
        });
        setScale(clampedScale);
    };

    return (
        <div
            className="minimap-container"
            style={{
                width: mapWidth,
                height: mapHeight,
                backgroundColor: boardBackgroundColor,
            }}
        >
            <Stage
                width={mapWidth}
                height={mapHeight}
                onWheel={handleWheel}
                onMouseDown={handleMapClick}
            >
                <Layer
                    scaleX={minimapScale}
                    scaleY={minimapScale}
                    x={-bounds.x * minimapScale}
                    y={-bounds.y * minimapScale}
                >
                    <Rect
                        x={bounds.x}
                        y={bounds.y}
                        width={bounds.width}
                        height={bounds.height}
                        fill="transparent"
                    />
                    {lines.map((line, i) => {
                        const flatPoints = typeof line.points[0] === 'object' && line.points[0] !== null
                            ? (line.points as any[]).reduce((acc, p) => {
                                acc.push(p.x, p.y);
                                return acc;
                            }, [] as number[])
                            : line.points;

                        return (
                            <Line
                                key={i}
                                points={flatPoints as number[]}
                                stroke={line.color}
                                strokeWidth={line.width * 2}
                                tension={0.5}
                                lineCap="round"
                                lineJoin="round"
                                listening={false}
                            />
                        );
                    })}
                </Layer>

                <Layer>
                    <Rect
                        name="viewbox"
                        x={viewX}
                        y={viewY}
                        width={viewWidth}
                        height={viewHeight}
                        stroke="#ff0000"
                        strokeWidth={2}
                        draggable
                        onDragMove={handleDragMove}
                        onMouseEnter={(e) => {
                            const container = e.target.getStage()?.container();
                            if (container) container.style.cursor = 'grab';
                        }}
                        onMouseLeave={(e) => {
                            const container = e.target.getStage()?.container();
                            if (container) container.style.cursor = 'default';
                        }}
                        onMouseDown={(e) => {
                            const container = e.target.getStage()?.container();
                            if (container) container.style.cursor = 'grabbing';
                        }}
                        onMouseUp={(e) => {
                            const container = e.target.getStage()?.container();
                            if (container) container.style.cursor = 'grab';
                        }}
                    />
                </Layer>
            </Stage>
        </div>
    );
};